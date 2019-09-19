/**
 * 文件MD5值check
 *
 * @param filePath 文件路径
 * @param md5 文件md5值
 * @param forceCheck 是强制检查，是-必检，否-md5值非空则进行检查
 */
void call (String filePath, String md5, boolean forceCheck = true) {
    fileName = sh(script: "basename \'${filePath}\'", returnStdout: true).trim()
    // md5值非空或者是强制检查情形下，需要进行MD5值校验
    if (forceCheck || !md5.isEmpty()) {
        if (sh(script: "md5sum \'${filePath}\' | sed \'s/\\ .*\$//\'", returnStdout: true).trim() != md5.toLowerCase()) {
            if (md5.isEmpty()) {
                error "${fileName} didn't have MD5 value"
            } else {
                error "${fileName} MD5 check did not match"
            }
        }
    }
}