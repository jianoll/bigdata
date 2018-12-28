package com.giggs;

import org.apache.commons.cli.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        final Options options = new Options();
        final Option option = new Option("c", true, "config file path");
        final Option option02 = new Option("f", true, "source file path");
        final Option option03 = new Option("m", true, "mapping file path");
        options.addOption(option);
        options.addOption(option02);//需要先定义再添加到options里面，不然无法解析参数会报错
        options.addOption(option03);
        final CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (final ParseException e) {
            throw new Exception("parser command line error", e);
        }

        String configPath = null;
        if (cmd.hasOption("c")) {
            configPath = cmd.getOptionValue("c");
        } else {
            System.err.println("please input the config file path by -c option");
            System.exit(1);
        }

        String sourceFile = null;
        if (cmd.hasOption("f")) {
            sourceFile = cmd.getOptionValue("f");
        } else {
            System.err.println("please input the source file file path by -f option");
            System.exit(1);
        }
        String mappingFile = null;
        if (cmd.hasOption("m")) {
            mappingFile = cmd.getOptionValue("m");
        } else {
            System.err.println("please input the mapping file path by -m option");
            System.exit(1);
        }
        System.out.println(String.format("%s,  %s  ,%s", configPath,sourceFile,mappingFile));
        CsvLoader csvLoader = new CsvLoader();


    }

}
