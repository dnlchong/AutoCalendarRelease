import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "";


        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        //this loop allows the user to enter data
        //this part of the code appends a new row (doesn't just edit, so it's super useful)
        boolean newInput = true;
        while (newInput) {
            Scanner inputScanner = new Scanner(System.in);
            System.out.println("If you wish to add an entry, add in format Day, Hour, Name, Event Type, Description.");
            System.out.println("Else, type n");
            String input = inputScanner.nextLine();
            if(input.equals("n")) {
                newInput = false;
            }
            else{
                String appendRange = "Event List!A2:E2";
                ValueRange appendTest = new ValueRange()
                        .setValues(Arrays.asList(
                                Arrays.asList((Object[]) input.split(", "))
                        ));
                AppendValuesResponse appendResult = service.spreadsheets().values()
                        .append(spreadsheetId, appendRange, appendTest)
                        .setValueInputOption("USER_ENTERED")
                        .setInsertDataOption("INSERT_ROWS")
                        .setIncludeValuesInResponse(true)
                        .execute();
            }
        //this part of the code sorts the event list
        SortSpec specs = new SortSpec()
                .setDimensionIndex(0)
                .setSortOrder("ASCENDING");
        SortSpec specs2 = new SortSpec()
                .setDimensionIndex(1)
                .setSortOrder("ASCENDING");
        GridRange sortGridRange = new GridRange()
                .setSheetId(688292375)
                .setStartRowIndex(1);
        SortRangeRequest rangesort = new SortRangeRequest()
                .setRange(sortGridRange)
                .setSortSpecs(Arrays.asList(specs, specs2));
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setSortRange(rangesort));
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
        //this part removes all existing data in variable sheets
        DeleteDimensionRequest delete2Weeks = new DeleteDimensionRequest()
                .setRange(
                        new DimensionRange().setSheetId()
                                .setDimension("ROWS")
                                .setStartIndex(1)
                                .setEndIndex(200)
                );
        DeleteDimensionRequest delete1Month = new DeleteDimensionRequest()
                .setRange(
                        new DimensionRange().setSheetId()
                                .setDimension("ROWS")
                                .setStartIndex(1)
                                .setEndIndex(200)
                );
        DeleteDimensionRequest delete1Year = new DeleteDimensionRequest()
                .setRange(
                        new DimensionRange().setSheetId()
                                .setDimension("ROWS")
                                .setStartIndex(1)
                                .setEndIndex(200)
                );
        AppendDimensionRequest add2Weeks = new AppendDimensionRequest()
                .setDimension("ROWS")
                .setLength(200)
                .setSheetId();
        AppendDimensionRequest add1Month = new AppendDimensionRequest()
                .setDimension("ROWS")
                .setLength(200)
                .setSheetId();
        AppendDimensionRequest add1Year = new AppendDimensionRequest()
                .setDimension("ROWS")
                .setLength(200)
                .setSheetId();
        List<Request> dimensionRequests = new ArrayList<>();
        requests.add(new Request().setDeleteDimension(delete2Weeks));
        requests.add(new Request().setDeleteDimension(delete1Month));
        requests.add(new Request().setDeleteDimension(delete1Year));
        requests.add(new Request().setAppendDimension(add2Weeks));
        requests.add(new Request().setAppendDimension(add1Month));
        requests.add(new Request().setAppendDimension(add1Year));
        BatchUpdateSpreadsheetRequest dims = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId, dims).execute();
        //this part defines the system calendar
        Calendar systemCalendar = Calendar.getInstance();
        int index = 0;
        int currentDayOfEternity = (systemCalendar.get(Calendar.DAY_OF_YEAR))+365*(systemCalendar.get(Calendar.YEAR));


        //this part creates the two week filtered calendar
        ValueRange result = service.spreadsheets().values()
                .get(spreadsheetId, "Event List!A2:E")
                .execute();
        List<List<Object>> TwoWeekValues = new ArrayList<>();
        for(int i = 0; i<result.getValues().size(); i++){
            String dateComparator = (String)result.getValues().get(i).get(0);
            Calendar instanceCalendar = Calendar.getInstance();
            instanceCalendar.set(Integer.parseInt(dateComparator.split("/")[2]), Integer.parseInt(dateComparator.split("/")[0])-1, Integer.parseInt(dateComparator.split("/")[1]));
            int instanceDayOfEternity = instanceCalendar.get(Calendar.DAY_OF_YEAR)+instanceCalendar.get(Calendar.YEAR)*365;
            if(instanceDayOfEternity-14 <= currentDayOfEternity && !(instanceDayOfEternity<currentDayOfEternity)){
                TwoWeekValues.add(result.getValues().get(i));
            }
        }
        ValueRange TwoWeeks = new ValueRange();
        TwoWeeks.setValues(TwoWeekValues);
        AppendValuesResponse appendWeek = service.spreadsheets().values()
                .append(spreadsheetId, "2 Weeks!A2:A100", TwoWeeks)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("OVERWRITE")
                .setIncludeValuesInResponse(true)
                .execute();
        //this part of the code creates the one month calendar
        List<List<Object>> OneMonthValues = new ArrayList<>();
        for(int i = 0; i<result.getValues().size(); i++){
            String dateComparator = (String)result.getValues().get(i).get(0);
            Calendar instanceCalendar = Calendar.getInstance();
            instanceCalendar.set(Integer.parseInt(dateComparator.split("/")[2]), Integer.parseInt(dateComparator.split("/")[0])-1, Integer.parseInt(dateComparator.split("/")[1]));
            int instanceDayOfEternity = instanceCalendar.get(Calendar.DAY_OF_YEAR)+instanceCalendar.get(Calendar.YEAR)*365;
            if(instanceDayOfEternity-31 <= currentDayOfEternity && !(instanceDayOfEternity<currentDayOfEternity)){
                OneMonthValues.add(result.getValues().get(i));
            }
        }
        ValueRange OneMonth = new ValueRange();
        OneMonth.setValues(OneMonthValues);
        AppendValuesResponse appendMonth = service.spreadsheets().values()
                .append(spreadsheetId, "1 Month!A2:A100", OneMonth)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("OVERWRITE")
                .setIncludeValuesInResponse(true)
                .execute();
        //this part of the code creates the 1 year calendar
        List<List<Object>> OneYearValues = new ArrayList<>();
        for(int i = 0; i<result.getValues().size(); i++){
            String dateComparator = (String)result.getValues().get(i).get(0);
            Calendar instanceCalendar = Calendar.getInstance();
            instanceCalendar.set(Integer.parseInt(dateComparator.split("/")[2]), Integer.parseInt(dateComparator.split("/")[0])-1, Integer.parseInt(dateComparator.split("/")[1]));
            int instanceDayOfEternity = instanceCalendar.get(Calendar.DAY_OF_YEAR)+instanceCalendar.get(Calendar.YEAR)*365;
            if(instanceDayOfEternity-365 <= currentDayOfEternity && !(instanceDayOfEternity<currentDayOfEternity)){
                OneYearValues.add(result.getValues().get(i));
            }
        }
        ValueRange OneYear = new ValueRange();
        OneYear.setValues(OneYearValues);
        AppendValuesResponse appendYear = service.spreadsheets().values()
                .append(spreadsheetId, "1 Year!A2:A100", OneYear)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("OVERWRITE")
                .setIncludeValuesInResponse(true)
                .execute();
        }
    }
}
