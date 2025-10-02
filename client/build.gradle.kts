val npm = if (System.getProperty("os.name").lowercase().contains("windows")) "npm.cmd" else "npm"

tasks.register<Exec>("build") {
    workingDir(project.projectDir)

    commandLine(npm, "run", "build")
}

tasks.register<Exec>("clean") {
    workingDir(project.projectDir)

    commandLine(npm, "run", "clean")
}