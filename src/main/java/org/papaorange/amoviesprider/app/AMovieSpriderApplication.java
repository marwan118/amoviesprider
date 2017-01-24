package org.papaorange.amoviesprider.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "org.papaorange.amoviesprider.*")
@EnableScheduling
public class AMovieSpriderApplication
{

    public static void main(String[] args)
    {
	SpringApplication.run(AMovieSpriderApplication.class, args);
    }

}
//
//@Component
//class StartupHousekeeper
//{
//
//    @EventListener(ContextRefreshedEvent.class)
//    void contextRefreshedEvent()
//    {
//	new TorrentDownloadTask().downloadTorrentTask();
//    }
//}