/**
 * Git仓库版本拉取 -- 支持基于分支/标签 以及 Submodule子仓库拉取
 *
 * @param gitBranchOrTag Git仓库分支 或 标签
 * @param gitRepository  Git仓库地址
 * @param gitCredentialsId  Git拉取凭证ID 【若存在Submodule子仓库，则该凭证需要有子仓库拉取权限】
 * @return
 */
void call(String gitBranchOrTag, String gitRepository, String gitCredentialsId) {
    checkout([$class: 'GitSCM',
              branches: [[name: "${gitBranchOrTag}"]],
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
}
