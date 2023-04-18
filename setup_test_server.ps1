if(!(Test-Path "./BuildTools.jar")) {
    Invoke-WebRequest -OutFile BuildTools.jar -Uri https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
}
$MINECRAFT_VERSION= if ($args[0]) { $args[0] } else { "1.12.2" }
[System.IO.Directory]::CreateDirectory($PSScriptRoot + "/test_server/$MINECRAFT_VERSION")
[System.IO.Directory]::CreateDirectory($PSScriptRoot + "/test_server/build_tools")
Set-Location ./test_server/build_tools
java -jar ../../BuildTools.jar --rev="$MINECRAFT_VERSION" --output-dir="../$MINECRAFT_VERSION"
Set-Location ../..