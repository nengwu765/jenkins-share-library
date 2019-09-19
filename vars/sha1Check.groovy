/**
 * 文件SHA1值check
 *
 * @param filePath 文件路径
 * @param sha1 文件md5值
 * @param forceCheck 是强制检查，是-必检，否-md5值非空则进行检查
 */
void call (String filePath, String sha1, boolean forceCheck = true) {
    fileName = sh(script: "basename \'${filePath}\'", returnStdout: true).trim()
    // md5值非空或者是强制检查情形下，需要进行SHA1值校验
    if (forceCheck || !sha1.isEmpty()) {
        if (sh(script: "sha1sum \'${filePath}\' | sed \'s/\\ .*\$//\'", returnStdout: true).trim() != sha1.toLowerCase()) {
            if (sha1.isEmpty()) {
                error "${fileName} didn't have SHA1 value"
            } else {
                error "${fileName} SHA1 check did not match"
            }
        }
    }
}