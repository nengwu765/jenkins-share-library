/**
 * Git仓库版本拉取 -- 支持基于分支/标签 以及 Submodule子仓库拉取
 *
 * @param fetchMode Git代码拉取方式：branch-基于分支; tag-基于标签
 * @param gitBranchOrTag Git仓库分支 或 标签
 * @param gitRepository  Git仓库地址
 * @param gitCredentialsId  Git拉取凭证ID 【若存在Submodule子仓库，则该凭证需要有子仓库拉取权限】
 * @param gitEvent  git拉取事件,基于分支或Tag拉取,则置空; MergeRequest则为:"MR"
 * @param targetBranch 若gitEvent为"MR",此处为MR目标分支
 * @return
 */
void call(String fetchMode,String gitBranchOrTag, String gitRepository, String gitCredentialsId, String gitEvent = '', String targetBranch = '') {
    if (fetchMode == 'branch') {
        gitRef = "refs/heads/${gitBranchOrTag}"
    } else if (fetchMode == 'tag') {
        gitRef = "refs/tags/${gitBranchOrTag}"
    } else {
        error "Git fetch mode must be branch or tag"
    }
    if (gitEvent == '') {
        checkout([$class: 'GitSCM',
                  branches: [[name: "${gitRef}"]],
                  doGenerateSubmoduleConfigurations: false,
                  extensions: [[$class: 'SubmoduleOption',
                                disableSubmodules: false,
                                parentCredentials: true,
                                recursiveSubmodules: true,
                                reference: '',
                                trackingSubmodules: false]],
                  gitTool: 'Default',
                  submoduleCfg: [],
                  userRemoteConfigs: [[url: "${gitRepository}",
                                       credentialsId: "${gitCredentialsId}"]]
        ])
    } else if (gitEvent == "MR" && targetBranch != '') {
        checkout changelog: true, poll: true, scm: [
                $class: 'GitSCM',
                branches: [[name: "${gitRef}"]],
                extensions: [[$class: 'SubmoduleOption',
                              disableSubmodules: false,
                              parentCredentials: true,
                              recursiveSubmodules: true,
                              reference: '',
                              trackingSubmodules: false],
                             [$class: 'PreBuildMerge',
                              options: [fastForwardMode: 'FF',
                                        mergeRemote: 'origin',
                                        mergeStrategy: 'DEFAULT',
                                        mergeTarget: "${targetBranch}"]]],
                userRemoteConfigs: [[name: 'origin', url: "${gitRepository}",
                                     credentialsId: "${gitCredentialsId}"]]]
    } else {
        error "Git event err or do not support"
    }

}
