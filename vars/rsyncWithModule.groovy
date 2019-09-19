/**
 *  rsync 使用Module远程同步
 *
 * @param ip rsync服务器IP
 * @param port rsync服务器监听端口
 * @param path rsync本地同步绝对路径
 * @param module rsync同步模块
 * @param user  rsync同步用户，指定传输文件为指定用户
 * @param customParams 定制rsync参数，不允许出现"delete"关键字，如：--exclude=PATTERN --include=PATTERN --exclude-from=FILE --include-from=FILE
 * @param bwlimit
 */
void call (String ip, int port, String path, String module, String user = '', String customParams = '', int bwlimit = 8192) {
    if (customParams ==~ /.*delete.*/) {
        error "\"delete\" operation is forbidden in rsync command!"
    }

    if (user != '') {
        user += '@'
    }

    sh "rsync -avzP " +
            "--bwlimit=${bwlimit}  " +
            "--port=${port} " +
            "${path} " +
            "${customParams} " +
            "${user}${ip}::${module}/"
}