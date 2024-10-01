import axios from "axios";
import {ElMessage} from 'element-plus'


const authItemName = "access_token"

const defaultFailure = (message, code, url) => {
    console.warn(`请求失败：${url}，错误码：${code}，错误信息：${message}`);
    ElMessage.warning(message)
}


const defaultError = (e) => {
    console.error(e)
    ElMessage.warning('请求失败，请检查网络连接')
}

function storeAccessToken(token, remember, expire) {
    const authObj = {token: token, expire: expire}
    const str = JSON.stringify(authObj)
    if (remember) {
        localStorage.setItem(authItemName, str)
    } else {
        sessionStorage.setItem(authItemName, str)
    }
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    if (!str) return null
    const authObj = JSON.parse(str)
    if (authObj.expire < Date.now()) {
        deleteAccessToken()
        ElMessage.warning('登录已过期，请重新登录')
        return null
    }
    return authObj.token
}

function internalPost(url, data, header, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(e => {
        error(e)
    })
}

function internalGet(url, header, success, failure = defaultFailure, error = defaultError) {
    axios.get(url, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(e => {
        error(e)
    })
}

function login(username, password, remember, success, failure = defaultFailure) {
    internalPost('/api/auth/login', {
        username: username,
        password: password
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        ElMessage.success(`登录成功, 欢迎${data.username}`)
        storeAccessToken(data.token, remember, data.expire)
        success(data)
    }, failure)
}

function getAccessHeader() {
    var token = takeAccessToken();
    if (token) {
        return {
            'Authorization': `Bearer ${token}`
        }
    }
    return {}
}

function get(url, success, failure = defaultFailure) {
    internalGet(url, getAccessHeader(), success, failure)
}

function post(url, data, success, failure = defaultFailure) {
    internalPost(url, data, getAccessHeader(), success, failure)
}

function logout(success, failure = defaultFailure) {
    get('/api/auth/logout', () => {
        deleteAccessToken()
        ElMessage.success('注销成功')
        success()
    }, failure)
}


function unauthorized() {
    const token = takeAccessToken()
    return !token;

}

export {login, logout, get, post, unauthorized, deleteAccessToken}