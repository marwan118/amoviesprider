package org.papaorange.amoviesprider.app;

import org.papaorange.amoviesprider.control.GetMovieController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = GetMovieController.class)
public class AMovieSpriderApplication
{

    public static void main(String[] args)
    {
	SpringApplication.run(AMovieSpriderApplication.class, args);
    }
}
