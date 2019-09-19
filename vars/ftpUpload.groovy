/**
 * FTP文件上传  -- // 参数参考说明：https://jenkins.io/doc/pipeline/steps/publish-over-ftp
 * @param configName  在Jenkins全局设置的FTP Server名称
 * @param sourceFiles  上传源文件，支持正则匹配，如[递归目录层级使用'**']：'*.apk,public/*'
 * @param remoteDir  上传子目录路径，注此路径会追加到全局设置的FTP Server的Remote Directory之后
 * @param excludes  排除上传文件，支持正则匹配，如[递归目录层级使用'**']： '*.log,*.tmp,.git/'
 * @param removePrefix  去除上传文件的路径前缀，避免ftp服务器中生成无用的目录路径,注：所有上传文件必须都有该前缀，否则报错
 */
void call(String configName, String sourceFiles, String remoteDir = '', String excludes = '', String removePrefix = '') {
    ftpPublisher alwaysPublishFromMaster: true, // 使用Jenkins master ftp传输文件，可以提升复杂环境的部署效率
            continueOnError: false, // 当前FTP server部署失败，可以部署至其他server
            failOnError: true,  // ftp上传报错，标记此次构建为：unstable
            masterNodeName: "${NODE_NAME}",  // 设置Jenkins的节点名称
            paramPublish: null,  // 暂未知，默认为null，否则Jenkins报错
            publishers: [
                    [configName: "${configName}",  // 在Jenkins全局设置的FTP Server名称
                     verbose: false,   // 日志是否输出至Jenkins console
                     transfers: [
                             [sourceFiles: "${sourceFiles}",  // 上传源文件，支持正则匹配，如：'**/*.apk,public/**/*'
                              excludes: "${excludes}",  // 排除上传文件，支持正则匹配，如： '**/*.log,**/*.tmp,.git/'
                              remoteDirectory: "${remoteDir}", // 上传子目录路径，注此路径会追加到全局设置的FTP Server的Remote Directory之后
                              removePrefix: "${removePrefix}", // 去除上传文件的路径前缀，避免ftp服务器中生成无用的目录路径，注：所有上传文件必须都有该前缀，否则报错
                              asciiMode: false, // 使用ASCII文本文件修复行结束符在不同的操作系统之间转移
                              remoteDirectorySDF: false, // 包括远程目录的时间戳
                              flatten: false, // 只在服务器上创建文件,不要创建目录，注：不允许有同名文件存在，否则报错
                              cleanRemote: false, // 是否在传输前，清空ftp服务的上传目录
                              makeEmptyDirs: false, // 是否支持上传空目录
                              noDefaultExcludes: false, // 无默认排除选项
                              patternSeparator: '[, ]+'] // 用于区分源文件和排除文件的正则模式，默认：'[, ]+'
                     ],
                     useWorkspaceInPromotion: false, // 使用当前构建工作区作为源文件根目录
                     usePromotionTimestamp: false, // 使用ftp上传时间戳设置
                     ftpRetry: [
                             retries: 3, // 重传次数
                             retryDelay: 1, // 多少毫秒后重新上传
                     ],
                     ftpLabel: null // 为当前ftp上传设置标签
                    ]
            ]
}