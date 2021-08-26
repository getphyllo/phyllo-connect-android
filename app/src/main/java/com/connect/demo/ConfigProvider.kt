package com.connect.demo
import android.text.TextUtils
import com.getphyllo.PhylloConnect
import java.lang.IllegalArgumentException

object ConfigProvider {
    private var config: Config? = null

    fun saveConfig(configData: Config?) {
        if (configData == null || TextUtils.isEmpty(configData.clientId) || TextUtils.isEmpty(
                configData.clientSecret)) throw IllegalArgumentException("Configuration is missing")
        config = configData
    }

    fun getConfig(): Config? {
        return config
    }

    fun getBaseUrl(): String {
        for (env in PhylloConnect.ENVIRONMENT.values()) {
            if (env.name.toLowerCase() == config?.environment) {
                return env.baseUrl
            }
        }
        return PhylloConnect.ENVIRONMENT.PRODUCTION.baseUrl
    }

    fun getEnvironment() :PhylloConnect.ENVIRONMENT {
        for (env in PhylloConnect.ENVIRONMENT.values()) {
            if (env.name.toLowerCase() == config?.environment) {
                return env
            }
        }
        return PhylloConnect.ENVIRONMENT.PRODUCTION
    }
}