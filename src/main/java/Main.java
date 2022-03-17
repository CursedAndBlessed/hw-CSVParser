import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeJsonToFile(json, "data.json");

        List<Employee> employeeList = parseXML("data.xml");
        String jsonXML = listToJson(employeeList);
        writeJsonToFile(json, "data2.json");

    }

        private static List<Employee> parseXML(String filename) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new File(filename));
                return read(doc.getDocumentElement());
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        private static List<Employee> read(Node node) {
            List<Employee> emplist = new ArrayList<>();
            NodeList nodeList = node.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node_ = nodeList.item(i);
                if (Node.ELEMENT_NODE == node_.getNodeType()) {
                    Element element = (Element) node_;

                    try {
                        Employee employee = new Employee();

                        employee.id =
                                Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                        employee.firstName =
                                element.getElementsByTagName("firstName").item(0).getTextContent();
                        employee.lastName =
                                element.getElementsByTagName("lastName").item(0).getTextContent();
                        employee.country =
                                element.getElementsByTagName("country").item(0).getTextContent();
                        employee.age =
                                Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                        emplist.add(employee);

                    } catch (NumberFormatException | NullPointerException e) {
                        System.out.println("Не удалось распарсить xml файл в объект(ы) класса Employee");
                        return null;
                    }
                }
            }
            return emplist;
        }
        private static List<Employee> parseCSV (String[]columnMapping, String fileName){
            List<Employee> list = null;

            try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
                ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Employee.class);
                strategy.setColumnMapping(columnMapping);

                CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                        .withMappingStrategy(strategy)
                        .build();

                list = csvToBean.parse();
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                return list;
            }
            return list;
        }

        private static String listToJson (List < Employee > list) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Type listType = new TypeToken<List<Employee>>() {
            }.getType();
            return gson.toJson(list, listType);
        }

        private static void writeJsonToFile (String json, String filename){
            try (FileWriter writer = new FileWriter(filename, false)) {
                writer.write(json);
                writer.flush();
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }



