package org.kpi;

import com.teamdev.jxbrowser.engine.EngineOptions;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.browser.*;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

public class Viewer {

    public static void main(String[] args) {
        view();
    }
    public static void view() {
        Engine engine = Engine.newInstance(EngineOptions.newBuilder(HARDWARE_ACCELERATED).
                licenseKey("1BNDHFSC1G8BMR8UHABWWHW1CF3HTD5PPQAKYMLURPK5DH1OT7AENOEI65R5TQZCYYLKON").build());

        Browser browser1 = engine.newBrowser();
        browser1.navigation().loadUrl("http://localhost:8080/api/files/drop");

        Browser browser2 = engine.newBrowser();
        browser2.navigation().loadUrl("http://localhost:4040/jobs/");

        Browser browser3 = engine.newBrowser();
        browser3.navigation().loadUrl("http://localhost:8080/config");

        Browser browser4 = engine.newBrowser();
        browser4.navigation().loadUrl("http://localhost:8080/contract");


        SwingUtilities.invokeLater(() -> {
            // Creating Swing component for rendering web content
            // loaded in the given Browser instance
            BrowserView view1 = BrowserView.newInstance(browser1);
            BrowserView view2 = BrowserView.newInstance(browser2);
            BrowserView view3 = BrowserView.newInstance(browser3);
            BrowserView view4 = BrowserView.newInstance(browser4);

            // Creating and displaying Swing app frame
            JFrame frame = new JFrame("IntegrityMaster: моніторинг та перевірка цілісності");

            // Creating second tab
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Завантаження та запуск", view1);
            tabbedPane.addTab("Моніторинг процесів", view2);
            tabbedPane.addTab("Конфігурація системи", view3);
            tabbedPane.addTab("Контракт програми", view4);

            // Adding refresh button
            JButton refreshButton = new JButton("Оновити");
            refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Refresh the currently selected tab
                    BrowserView currentView = (BrowserView) tabbedPane.getSelectedComponent();
                    currentView.getBrowser().navigation().reload();
                }
            });

            // Adding the refresh button to the frame
            frame.add(refreshButton, BorderLayout.NORTH);

            // Closing the engine when app frame is about to close
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.close();
                }
            });
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.add(tabbedPane, BorderLayout.CENTER);
            frame.setSize(1920, 1080);
            frame.setVisible(true);

        });

    }

}
