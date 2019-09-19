/**
 * 流水线执行结果回调操作
 *
 * @param artifacts 制品存储数据
 * @return
 */
void call(String callBackUrlsJson, Map artifacts = [:]) {
    if (callBackUrlsJson != null && callBackUrlsJson != '') {
        callBackUrls = readJSON text: callBackUrlsJson

        // 制品管理数据回传json格式
        artifactsJson = ''
        if (binding.variables.containsKey("ARTIFACTS")) {
            artifactsJson = JsonOutput.toJson(artifacts)
        }

        // 是否含 resultCallBackUrl 回调Url,需始终回调，故放在 always 中直接处理执行
        if (callBackUrls.containsKey('resultCallbackUrl') && !callBackUrls['resultCallbackUrl'].isEmpty()) {
            def resultCallbackUrl = callBackUrls['resultCallbackUrl']
            // 是否绑定host进行回调
            if (resultCallbackUrl.containsKey('host') && resultCallbackUrl['host'] != '') {
                sh "curl -X POST " +
                        "--data-urlencode  \'result=${currentBuild.currentResult}\' " +
                        "--data-urlencode \'artifacts=${artifactsJson}\' " +
                        "-H 'Host: ${resultCallbackUrl['host']}' " +
                        "${resultCallbackUrl['url']}"
            } else {
                // 无host绑定，默认为线上，使用https协议传输，否则会有异常错误
                def callback_url = resultCallbackUrl['url'].replaceAll("http://", "https://")
                sh "curl -X POST " +
                        "--data-urlencode  \'result=${currentBuild.currentResult}\' " +
                        "--data-urlencode \'artifacts=${artifactsJson}\' " +
                        "${callback_url}"
            }
        }
    }
}
