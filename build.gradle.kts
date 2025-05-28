plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "3.1.1"
}

repositories {
    mavenCentral()
}

val javafxVersion = "21"

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainModule.set("localdrop")
    mainClass.set("org.deg.ui.NetworkTransferUI")
}

jlink {
    imageName.set("NetworkTransferUI")

    launcher {
        name = "network-transfer-ui"
    }

    jpackage {
        installerName = "NetworkTransferInstaller"
        appVersion = "1.0.0"
        // installerType = "exe" // Uncomment and set according to your platform
    }
}
