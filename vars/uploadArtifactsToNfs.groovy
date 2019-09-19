/**
 * 上传制品至Nexus仓库
 *
 * @param nexusRepository Nexus上传仓库
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
 *         eg: [
 *              'file1':[
 *                  'url':'https:down.demo.com/.../file1'  // 文件在nexus中的上传路径【即下载路径】
 *                  'md5':'xxxx' // 文件md5值
 *                  'sha1':'xxxx'   // 文件sha1值
 *               ],
 *         ]
 */
def call(String nexusRepository, String dir, String fileMatchRegex, String uploadPath, boolean isRecursion = false, Map artifacts = [:]) {
    if (sh(script: "ls ${dir}", returnStatus: true) == 0 ) {
        // 上传apk制品
        sh(script: "ls ${dir}", returnStdout: true).trim().split("\n").each { fileName ->
            if (sh(script: "test -f \'${dir}/${fileName}\'", returnStatus: true) == 0 ) {
                if (fileName ==~ /.+$fileMatchRegex$/ || fileName ==~ /^$fileMatchRegex.+/ || fileName ==~ /$fileMatchRegex/) {
                    // 上传地址
                    uploadUrl = "http://down.demo.com/mobile/${nexusRepository}/${uploadPath}/${fileName}"

                    // Copy制品文件至制品共享存储空间（/opt/artifactory/mobile-cicd/）
                    artifactTargetDir = "/opt/artifactory/mobile-cicd/" + "${nexusRepository}/${uploadPath}"
                    if (sh(script: "test -d \'${artifactTargetDir}\'", returnStatus: true) != 0) {
                        sh "mkdir -p ${artifactTargetDir}"
                    }
                    sh "cp \'${dir}/${fileName}\'  \'${artifactTargetDir}\' "

                    md5 = sh(script: "md5sum \'${dir}/${fileName}\' | sed \'s/\\ .*\$//\'", returnStdout: true).trim()
                    sha1 = sh(script: "sha1sum \'${dir}/${fileName}\' | sed \'s/\\ .*\$//\'", returnStdout: true).trim()
                    artifacts["${fileName}"] = [
                            'url': "${uploadUrl}",
                            'md5': "${md5}",
                            'sha1': "${sha1}",
                    ]
                }
            } else if ('' != fileName && isRecursion) {
                artifacts = uploadArtifactsToNfs(nexusRepository, "${dir}/${fileName}", fileMatchRegex, uploadPath, isRecursion, artifacts)
            }
        }
    }
    return artifacts
}
