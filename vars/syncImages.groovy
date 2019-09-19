/**
 * 同步 json 文件中的所有镜像
 * @param jsonText 传递给 helm 的 values
 */
void call(String jsonText,
          String tDomain = "tharbor.demo.com", String tUsername = 'tharbor', String tPassword = 'tsecret',
          String domain = "harbor.demo.com", String username = 'harbor', String password = 'secret') {
    def values = readJSON text: jsonText
    values.each { prop, val ->
        if (prop.indexOf("image.repository") >= 0) {
            String name = val
            String tagKey = prop.replaceFirst("image.repository", "image.tag")
            String tag = values."${tagKey}"
            if (tag == null) {
                // 闭包函数，所以 return 就相当于本次迭代跳过
                printf("%s has no tag, skipped.\n", name)
                return
            }

            String src = name + ":" + tag
            // 约束：tHarbor 中一定有对应的镜像
            String dst = src.replaceFirst(domain, tDomain)
            // tharbor -> harbor
            syncOne(dst, src, tDomain, tUsername, tPassword, domain, username, password)
            printf("image sync successful. %s ==> %s\n", src, dst)
        }
    }
}

/**
 * 同步镜像
 * @param src 镜像源地址
 * @param dst 镜像目的地址
 * @param tUser tHarbor 的用户名
 * @param tPwd tHarbor 的用户密码
 * @param user harbor 的用户名
 * @param pwd harbor 的用户密码
 * @return String 新的镜像地址
 */
void syncOne(String src, String dst,
             String tDomain, String tUsername, String tPassword,
             String domain, String username, String password) {
    // 只有这个账户有拉取所有镜像的权限
    sh "docker login ${tDomain} -u ${tUsername} -p ${tPassword}"
    def status = sh(returnStatus: true, script: "docker pull ${src}")
    if (0 != status) {
        // 未成功同步，但是允许继续执行
        echo "Warning: pull failed, but can skip."
        return
    }

    // 更换镜像域名
    sh "docker tag ${src} ${dst}"

    // 只有这个账户有推送到所有仓库的权限
    sh "docker login ${domain} -u ${username} -p ${password}"
    sh "docker push ${dst}"
}
