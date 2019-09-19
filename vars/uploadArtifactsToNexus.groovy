/**
 * 上传制品至Nexus仓库
 *
 * @param dir 检索目录
 * @param fileMatchRegex 文件正则匹配规则， 支持文件类型、文件前缀、文件全名，
 *        如文件：prefix_file.test,
 *        匹配规则：
 *              文件类型： fileMatchRegex = ".test"
 *              文件前缀： fileMatchRegex = "prefix_"
 *              文件全名： fileMatchRegex = "prefix_file.test"
 * @param uploadSubPath 制品上传路经
 * @param isRecursion 是否递归检索子目录,默认：否【注：同名文件会被覆盖】
 * @param artifacts 制品存储数据，Map结构，默认：空Map
 * @return Map 返回上传制品数据
 */
def call(String dir, String fileMatchRegex, String uploadPath, boolean isRecursion = false, Map artifacts = [:]) {
    if (sh(script: "ls ${dir}", returnStatus: true) == 0 ) {
        // 上传apk制品
        sh(script: "ls ${dir}", returnStdout: true).trim().split("\n").each { fileName ->
            if (sh(script: "test -f ${dir}/${fileName}", returnStatus: true) == 0 ) {
                if (fileName ==~ /.+$fileMatchRegex$/ || fileName ==~ /^$fileMatchRegex.+/ || fileName ==~ /$fileMatchRegex/) {
                    // 上传地址
                    uploadUrl = "https://nexus.2345.cn/repository/jenkins-product-repository-apk/${uploadPath}/${fileName}"

                    sh "curl -sL -w 'Upload the jar to the repository status code: %{http_code}\n' " +
                            "-u ${env.NEXUS_UPLOAD_CRED_USR}:${env.NEXUS_UPLOAD_CRED_PSW} " +
                            "--upload-file ${dir}/${fileName} " +
                            "${uploadUrl}"
                    artifacts["${fileName}"] = uploadUrl
                }
            } else if ('' != fileName && isRecursion) {
                artifacts = uploadArtifactsToNexus("${dir}/${fileName}", fileMatchRegex, uploadPath, isRecursion, artifacts)
            }
        }
    }
    return artifacts
}
