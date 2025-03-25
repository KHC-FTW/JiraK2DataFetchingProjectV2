package org.ha.ckh637.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ha.ckh637.config.SingletonConfig;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public final class AppIniService {
    private AppIniService(){}
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ObjectMapper getObjectMapper(){return AppIniService.OBJECT_MAPPER;}

    public static void readJsonConfigFile(String[] args){
        File jsonFile;
        String jsonPath;
        if (args.length < 1 || !(jsonFile = new File(args[0] + "\\SetUpConfig.json")).isFile()){
            try (Scanner getInput = new Scanner(System.in)) {
                do {
                    System.out.print("\nNo valid json file identified for set up. Please enter the json file path again.\n(Make sure the json file is named \"SetUpConfig.json\")\n> ");
                    jsonPath = getInput.nextLine();
                    jsonFile = new File(jsonPath + "\\SetUpConfig.json");
                } while (!jsonFile.isFile());
            }
        }else jsonPath = args[0];

        SingletonConfig.setIniInputPath(jsonPath);

        try {
            // Read JSON file and convert to JsonNode
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonFile);
            SingletonConfig singletonConfig = OBJECT_MAPPER.treeToValue(rootNode, SingletonConfig.class);
            SingletonConfig.updateSingletonConfig(singletonConfig);
            System.out.println("\nJson data saved with the following:\n");
            System.out.println(SingletonConfig.getInstance());
            System.out.println();
            DirectoryService.delTempDestDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
