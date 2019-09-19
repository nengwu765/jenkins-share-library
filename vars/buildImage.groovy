/**
 * 使用指定的参数构建 Docker 容器镜像
 * @param domain 域名
 * @param username 用户名
 * @param password 密码
 * @param project 项目名
 * @param app 应用名
 * @param tag 标签
 * @param dockerfile Dockerfile 文件的路径
 * @return void
 */
def call(String domain, String username, String password, String project, String app, String tag, String dockerfile = "") {
    // login harbor
    sh "docker login -u ${username} -p ${password} ${domain}"
    // build image
    if (dockerfile != "") {
        sh "docker build -t ${domain}/${project}/${app}:${tag} -f ${dockerfile} ."
    } else {
        sh "docker build -t ${domain}/${project}/${app}:${tag} ."
    }
    // push image to harbor
    sh "docker push ${domain}/${project}/${app}:${tag}"
}
