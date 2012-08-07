package org.ei.drishti.repository;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.dto.Action;
import org.ei.drishti.util.ActionBuilder;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class AllBeneficiariesTest {
    @Mock
    private ChildRepository childRepository;
    @Mock
    private MotherRepository motherRepository;

    private AllBeneficiaries allBeneficiaries;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allBeneficiaries = new AllBeneficiaries(motherRepository, childRepository);
    }

    @Test
    public void shouldHandleDifferentTypesOfActions() throws Exception {
        Action action = ActionBuilder.actionForCreateMother("Case X");
        allBeneficiaries.handleAction(action);
        String referenceDate = LocalDate.now().toString();
        verify(motherRepository).add(new Mother("Case X", "ecCaseId", "thaayiCardNumber", referenceDate));

        action = ActionBuilder.actionForUpdateBeneficiary();
        allBeneficiaries.handleAction(action);
        verify(childRepository).close("Case X");

        when(motherRepository.find("Case Mom")).thenReturn(new Mother("Case Mom", "EC Case 1", "TC 1", "2012-06-08"));
        Action childAction = ActionBuilder.actionForCreateChild("Case Mom");
        allBeneficiaries.handleAction(childAction);
        verify(childRepository).addChildForMother(new Mother("Case Mom", "EC Case 1", "TC 1", "2012-06-08"), "Case X", referenceDate, "female");
    }
}
