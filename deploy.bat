:: maven先部署到本地git仓库
call mvn deploy -DskipTests -DaltDeploymentRepository=^
maven.repo::default::file://E:/Extensions/RemoteMavenRepository/repository

:: 进入git仓库，然后提交并推送
cd /d E:\Extensions\RemoteMavenRepository\repository
git add . && ^
git commit -m update && ^
git push