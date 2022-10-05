package com.internship.rushhour.domain.activity.service;

import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.activity.models.ActivityDTO;
import com.internship.rushhour.domain.activity.models.ActivityResponseDTO;
import com.internship.rushhour.domain.activity.repository.ActivityRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.mappers.ActivityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {
    @Mock
    ActivityMapper activityMapper;

    @Mock
    ActivityRepository activityRepository;

    @InjectMocks
    ActivityServiceImpl activityService;

    @BeforeEach
    void beforeEach(){
        // mapper has become too complex to manually simulate, will return
        // a randomised object of the same type until better solution
        lenient().when(activityMapper.entityToDtoResponse(isA(Activity.class))).thenReturn(
                TestObjectFactory.generateActivityResponseDto()
        );
        lenient().when(activityMapper.entityToDto(isA(Activity.class))).thenReturn(
                TestObjectFactory.generateActivityDto()
        );
        lenient().when(activityMapper.dtoToEntity(isA(ActivityDTO.class))).thenReturn(
                TestObjectFactory.generateRandomisedActivity()
        );
    }

    @Test
    void deleteWhenResourceNotExists(){
        when(activityRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> {
            activityService.delete(20L);
        });
    }

    @Test
    void getPaginatedTest(){
        List<Activity> activities = new ArrayList<>();
        activities.add(TestObjectFactory.generateRandomisedActivity());
        activities.add(TestObjectFactory.generateRandomisedActivity());
        activities.add(TestObjectFactory.generateRandomisedActivity());
        activities.add(TestObjectFactory.generateRandomisedActivity());
        activities.add(TestObjectFactory.generateRandomisedActivity());

        when (activityRepository.findAll(isA(Pageable.class))).thenAnswer(i ->{
            Pageable pageable = (Pageable) i.getArguments()[0];
            return new PageImpl<Activity>(activities.subList(0, pageable.getPageSize()));
        });

        int pageSize = 3;
        Pageable pageable = Pageable.ofSize(pageSize);
        Page<ActivityResponseDTO> employeePage = activityService.getPaginated(pageable);
        List<ActivityResponseDTO> employeeList = employeePage.stream().toList();

        assertThat(employeeList.size()).isEqualTo(pageSize);
        assertThat(employeePage).isNotEmpty();
    }
}