import groovy.json.JsonOutput
import java.net.URLEncoder

/**
 * 流水线执行结果回调操作
 *
 * @param callBackUrlsJson 回调Url地址Json数据
 * @param artifacts 制品存储数据
 * @param stageState 阶段构建状态字段
 * @return
 */
void call(String callBackUrlsJson, Map artifacts = [:], String stageState = '') {
    if (callBackUrlsJson != null && callBackUrlsJson != '') {
        callBackUrls = readJSON text: callBackUrlsJson

        // 制品管理数据回传json格式, 同时将内容进行URLEncoder,避免特殊字符回调出错
        artifactsJson = URLEncoder.encode(JsonOutput.toJson(artifacts))

        // 以临时文件形式回传数据，防止回传信息量过大，curl出现argument list too long
        sh "echo \'artifacts=${artifactsJson}\' > tmpCallBackParamsFile"

        // 是否含 resultCallBackUrl 回调Url,需始终回调，故放在 always 中直接处理执行
        if (callBackUrls.containsKey('resultCallbackUrl') && !callBackUrls['resultCallbackUrl'].isEmpty()) {
            def resultCallbackUrl = callBackUrls['resultCallbackUrl']
            // 阶段构建和执行最终结果做区分，只有一个有回调值
            def result = (stageState != '') ? '' : currentBuild.currentResult
            // 是否绑定host进行回调
            if (resultCallbackUrl.containsKey('host') && resultCallbackUrl['host'] != '') {
                sh "curl -X POST " +
                        "-H 'Host: ${resultCallbackUrl['host']}' " +
                        "--connect-timeout 5 --retry 5 --retry-delay 5 " +
                        "--data-urlencode  \'result=${result}\' " +
                        "--data-urlencode  \'stageState=${stageState}\' " +
                        "-d @tmpCallBackParamsFile " +
                        "${resultCallbackUrl['url']}"
            } else {
                sh "curl -X POST " +
                        "--connect-timeout 5 --retry 5 --retry-delay 5 " +
                        "--data-urlencode  \'result=${result}\' " +
                        "--data-urlencode  \'stageState=${stageState}\' " +
                        "-d @tmpCallBackParamsFile " +
                        "${resultCallbackUrl['url']}"
            }
        }
    }
}
