package ru.anatoli.addressbook.tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.anatoli.addressbook.models.GroupData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static org.testng.Assert.assertEquals;

/**
 * Created by anatoli.anukevich on 6/27/2017.
 */
public class GroupModificationTests extends TestBase {
    @DataProvider
    public Iterator<Object[]> validDataForGroupModificationFromJson() throws IOException {
        String json = "";
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("src/test/resources/groupFileModification.json")))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                json += line;
                line = bufferedReader.readLine();
            }
        }
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<GroupData>>(){}.getType(); //List<GroupData>.class
        List<GroupData> list = gson.fromJson(json, collectionType);
        return list.stream().map((group) -> new Object[] {group}).iterator();
    }

    @BeforeMethod
    public void ensurePrecondition() {
        applicationManager.getNavigationHelper().goToGroupsPage();
        if (applicationManager.getDbHelper().getGroupSet().size() == 0) {
            GroupData groupData = new GroupData().withGroupName("Temp name")
                                                .withGroupHeader("Temp header")
                                                .withGroupFooter(null);
            applicationManager.getGroupHelper().createGroup(groupData);
        }
    }

    @Test(enabled = true, dataProvider = "validDataForGroupModificationFromJson")
    public void testGroupModification(GroupData groupData) {
        //Getting Set of GroupData object model BEFORE modification
        Set<GroupData> before = applicationManager.getDbHelper().getGroupSet();

        //Choosing the random Group that will be modified
        GroupData modifiedGroup = before.stream().findAny().get();

        groupData.withGroupId(modifiedGroup.getGroupId());

        applicationManager.getGroupHelper().modifyGroup(groupData);

        //Getting Set of GroupData object model AFTER modification
        Set<GroupData> after = applicationManager.getDbHelper().getGroupSet();

        //Asserting collections by SIZE
        assertEquals(before.size(), after.size());

        before.remove(modifiedGroup);
        before.add(groupData);

        //Asserting by COLLECTIONS
        assertEquals(before, after);

        //Asserting UI data vs DB data
        compareUiVsDbGroupData();
    }

    @Test(enabled = true, dataProvider = "validDataForGroupModificationFromJson")
    public void testGroupModificationWithoutSelection(GroupData groupData) {
        //Getting Set of GroupData object model BEFORE modification
        Set<GroupData> before = applicationManager.getDbHelper().getGroupSet();

        applicationManager.getGroupHelper().initiateGroupModification();
        String noticeTitle = applicationManager.getGroupHelper().getErrorMessageDuringDeletionAndModification();

        //Asserting by NOTICE title
        assertEquals(noticeTitle, "Notice");

        applicationManager.getGroupHelper().fillGroupForm(groupData);
        applicationManager.getGroupHelper().submitGroupModificationForm();
        String invalidIdMessage = applicationManager.getGroupHelper().getErrorTextAfterClickingUpdate();

        //Asserting by INVALID ID error message
        assertEquals(invalidIdMessage, "Invalid ID.");

        applicationManager.getGroupHelper().returnToGroupsPage();

        //Getting Set of GroupData object model AFTER modification
        Set<GroupData> after = applicationManager.getDbHelper().getGroupSet();

        //Asserting collections by SIZE
        assertEquals(before.size(), after.size());

        //Asserting by COLLECTIONS
        assertEquals(before, after);

        //Asserting UI data vs DB data
        compareUiVsDbGroupData();
    }
}
