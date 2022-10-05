:: maven先部署到本地git仓库
call gradlew -PremoteMavenRepositoryUrl=^
file://F:/Projects/Other/remote-maven-repo/repository ^
publish

:: 进入git仓库，然后提交并推送
cd /d F:\Projects\Other\remote-maven-repo
git add . && ^
git commit -m "Update honoka-sdk" && ^
git push
