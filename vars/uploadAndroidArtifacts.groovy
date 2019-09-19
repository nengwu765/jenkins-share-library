import groovy.transform.Field
@Field String NEXUS_REPOSITORY = 'artifacts-android'
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
 *         eg: [
 *              'file1':[
 *                  'url':'https:nexus.demo.com/.../file1'  // 文件在nexus中的上传路径【即下载路径】
 *                  'md5':'xxxx' // 文件md5值
 *                  'sha1':'xxxx'   // 文件sha1值
 *               ],
 *         ]
 */
def call(String dir, String fileMatchRegex, String uploadPath, boolean isRecursion = false, Map artifacts = [:]) {
    return uploadArtifactsToNfs(NEXUS_REPOSITORY, dir, fileMatchRegex, uploadPath, isRecursion, artifacts)
}
