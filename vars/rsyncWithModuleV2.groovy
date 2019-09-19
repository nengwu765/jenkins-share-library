/**
 *  rsync 使用Module远程同步
 *
 * @param host rsync服务器IP
 * @param port rsync服务器监听端口
 * @param module rsync同步模块
 * @param src rsync本地目录绝对路径
 * @param dst rsync远程目录绝对路径
 * @param user rsync同步用户，指定传输文件为指定用户
 * @param customParams 定制rsync参数，不允许出现"delete"关键字，如：--exclude=PATTERN --include=PATTERN --exclude-from=FILE --include-from=FILE
 * @param bwlimit
 * @note 如果指定了 RSYNC_PASSWORD 环境变量，将会被自动使用
 */
void call (String host, int port, String module, String src = '.', String dst = '', String user = '', String customParams = '', int bwlimit = 8192) {
    if (customParams ==~ /.*delete.*/) {
        error "\"delete\" operation is forbidden in rsync command!"
    }

    if (user != '') {
        user += '@'
    }

    sh "rsync -avzt " +
            "--bwlimit=${bwlimit} " +
            "--port=${port} " +
            "${customParams} " +
            "${src} " +
            "${user}${host}::${module}/${dst}"
}

