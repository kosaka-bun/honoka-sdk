package de.honoka.sdk.util.kotlin.various

import de.honoka.sdk.util.ui.ConsoleWindow
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.UIManager

object UiUtils {

    @Volatile
    private var hasStyle = false

    @Synchronized
    fun setUiStyleWithCurrentOs() {
        if(hasStyle) return
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        hasStyle = true
    }
}

fun JFrame.setDefaultIconImage() {
    val url = ConsoleWindow::class.java.getResource("/img/java.png")
    iconImage = ImageIcon(url).image
}
