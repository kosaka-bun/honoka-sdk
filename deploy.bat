set REMOTE_MAVEN_REPO_PATH=C:\Personal\Projects\Other\remote-maven-repo

:: 更新到最新的git仓库
cd /d "%REMOTE_MAVEN_REPO_PATH%"
git reset --hard HEAD
git pull --rebase

:: maven先部署到本地git仓库
cd /d "%~dp0"
call gradlew -PremoteMavenRepositoryUrl=^
file://C:/Personal/Projects/Other/remote-maven-repo/repository ^
publish

:: 进入git仓库，然后提交并推送
cd /d "%REMOTE_MAVEN_REPO_PATH%"
git add . && ^
git commit -m "Update honoka-sdk" && ^
git push
