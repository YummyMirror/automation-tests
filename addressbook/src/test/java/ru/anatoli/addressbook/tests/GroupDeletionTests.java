package ru.anatoli.addressbook.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.anatoli.addressbook.models.GroupData;
import java.util.Set;
import static org.testng.Assert.assertEquals;

/**
 * Created by anatoli.anukevich on 7/1/2017.
 */
public class GroupDeletionTests extends TestBase {
    @BeforeMethod
    public void ensurePrecondition() {
        applicationManager.getNavigationHelper().goToGroupsPage();
        if (applicationManager.getGroupHelper().getGroupSet().size() == 0) {
            GroupData groupData = new GroupData().withGroupName("test name")
                                                .withGroupHeader(null)
                                                .withGroupFooter(null);
            applicationManager.getGroupHelper().createGroup(groupData);
        }
    }

    @Test
    public void testGroupDeletion() {
        //Getting Set of GroupData object model BEFORE deletion
        Set<GroupData> before = applicationManager.getGroupHelper().getGroupSet();

        //Choosing the random Group that will be removed
        GroupData removedGroup = before.iterator().next();

        applicationManager.getGroupHelper().deleteGroup(removedGroup);

        //Getting Set of GroupData object model AFTER deletion
        Set<GroupData> after = applicationManager.getGroupHelper().getGroupSet();

        //Asserting collections by SIZE
        assertEquals(before.size() - 1, after.size());

        before.remove(removedGroup);

        //Asserting by COLLECTIONS
        assertEquals(before, after);
    }
}