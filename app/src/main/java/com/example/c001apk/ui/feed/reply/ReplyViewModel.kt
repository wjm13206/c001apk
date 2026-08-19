package com.example.c001apk.ui.feed.reply

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.logic.repository.NetworkRepo
import com.example.c001apk.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReplyViewModel @Inject constructor(
    private val networkRepo: NetworkRepo
) : ViewModel() {

    var type: String? = null
    var rid: String? = null

    val toastText = MutableLiveData<Event<String>>()
    val closePage = MutableLiveData<Event<Boolean>>()
    val showCaptcha = MutableLiveData<Event<Bitmap?>>()

    val replyData = HashMap<String, String>()
    val createFeedData = HashMap<String, String?>()
    var requestValidateData = HashMap<String, String?>()

    fun onPostReply() {
        viewModelScope.launch(Dispatchers.IO) {
            networkRepo.postReply(replyData, rid ?: "", type ?: "reply")
                .collect { result ->
                    val response = result.getOrNull()
                    if (response == null) {
                        toastText.postValue(Event(result.exceptionOrNull()?.message ?: "网络错误"))
                        return@collect
                    }

                    when {
                        response.status == 1 || response.data != null -> {
                            closePage.postValue(Event(true))
                        }

                        response.messageStatus == "err_request_captcha" ||
                            response.message == "err_request_captcha" -> {
                            getValidateCaptcha()
                        }

                        else -> {
                            toastText.postValue(Event(response.message ?: "回复失败"))
                        }
                    }
                }
        }
    }

    fun onPostCreateFeed() {
        viewModelScope.launch(Dispatchers.IO) {
            networkRepo.postCreateFeed(createFeedData)
                .collect { result ->
                    val response = result.getOrNull()
                    if (response == null) {
                        toastText.postValue(Event(result.exceptionOrNull()?.message ?: "网络错误"))
                        return@collect
                    }

                    when {
                        response.status == 1 || response.data != null -> {
                            closePage.postValue(Event(true))
                        }

                        response.messageStatus == "err_request_captcha" ||
                            response.message == "err_request_captcha" -> {
                            getValidateCaptcha()
                        }

                        else -> {
                            toastText.postValue(Event(response.message ?: "发布失败"))
                        }
                    }
                }
        }
    }

    private fun getValidateCaptcha() {
        viewModelScope.launch(Dispatchers.IO) {
            networkRepo.getValidateCaptcha("/v6/account/captchaImage?${System.currentTimeMillis() / 1000}&w=270=&h=113")
                .collect { result ->
                    val response = result.getOrNull()
                    if (response == null) {
                        toastText.postValue(Event(result.exceptionOrNull()?.message ?: "验证码获取失败"))
                        return@collect
                    }

                    val bitmap = response.body()?.byteStream()?.use {
                        BitmapFactory.decodeStream(it)
                    }
                    if (bitmap != null) {
                        showCaptcha.postValue(Event(bitmap))
                    } else {
                        toastText.postValue(Event("验证码获取失败"))
                    }
                }
        }
    }

    fun onPostRequestValidate() {
        viewModelScope.launch(Dispatchers.IO) {
            networkRepo.postRequestValidate(requestValidateData)
                .collect { result ->
                    val response = result.getOrNull()
                    if (response == null) {
                        toastText.postValue(Event(result.exceptionOrNull()?.message ?: "网络错误"))
                        return@collect
                    }

                    when {
                        response.status == 1 || response.data == "验证通过" -> {
                            if (type == "createFeed") {
                                onPostCreateFeed()
                            } else {
                                onPostReply()
                            }
                        }

                        !response.message.isNullOrEmpty() -> {
                            response.message?.let { message ->
                                toastText.postValue(Event(message))
                                if (message == "请输入正确的图形验证码" ||
                                    response.messageStatus == "err_request_captcha"
                                ) {
                                    getValidateCaptcha()
                                }
                            }
                        }

                        else -> {
                            toastText.postValue(Event("验证失败"))
                        }
                    }
                }
        }
    }

}
