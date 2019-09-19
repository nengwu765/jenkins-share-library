# Jenkins 共享库

## 方法列表

| 名字 | 描述 |
|:----:|:-----|
| `callBackAction` | 流水线执行结果回调操作 |
| `ftpUpload` | FTP文件上传 |
| `gitCodeFetch` | Git仓库版本拉取 |
| `md5Check` | 文件MD5值check |
| `rsyncWithModule` | 使用Module远程同步 |
| `sha1Check` | 文件SHA1值check |
| `syncImages` | 同步 json 文件中的所有镜像 |
| `syncOne` | 同步一个镜像（tharbor -> harbor） |
| `uploadAlgorithmArtifacts` | 上传制品至Nexus仓库 - 算法 |
| `uploadAndroidArtifacts` | 上传制品至Nexus仓库 - Android |
| `uploadBigdataArtifacts` | 上传制品至Nexus仓库 - 大数据 |
| `uploadArtifactsToNexus` | 上传制品至Nexus仓库 |

## 参考文档

[扩展共享库](https://jenkins.io/zh/doc/book/pipeline/shared-libraries/)
