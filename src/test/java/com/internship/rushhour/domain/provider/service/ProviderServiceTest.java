package com.internship.rushhour.domain.provider.service;

import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.models.ProviderDTO;
import com.internship.rushhour.domain.provider.models.ProviderDTOResponse;
import com.internship.rushhour.domain.provider.repository.ProviderRepository;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.mappers.ProviderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Mock
    ProviderMapper providerMapper;

    @Mock
    ProviderRepository providerRepository;

    @InjectMocks
    ProviderServiceImpl providerService;

    LocalTime mockStartTime;
    LocalTime mockEndTime;

    Set<DayOfWeek> testWorkingDays = new HashSet<>();

    @BeforeEach
    void beforeEach(){
        mockStartTime = LocalTime.parse("09:00");
        mockEndTime = LocalTime.parse("17:00");

        testWorkingDays.add(DayOfWeek.valueOf("MONDAY"));
        testWorkingDays.add(DayOfWeek.valueOf("FRIDAY"));

        lenient().when(providerMapper.entityToDto(isA(Provider.class))).thenAnswer(i ->{
            Provider p = (Provider) i.getArguments()[0];
            return new ProviderDTO(p.getName(), p.getWebsite(), p.getBusinessDomain(), p.getPhone(), p.getBusinessHoursStart()
            , p.getBusinessHoursEnd(), p.getWorkingDays().stream().map(Enum::name).collect(Collectors.toSet()));
        });

        lenient().when(providerMapper.dtoToEntity(isA(ProviderDTO.class))).thenAnswer(i ->{
            ProviderDTO p = (ProviderDTO) i.getArguments()[0];
            Provider provider = new Provider();
            provider.setBusinessDomain(p.businessDomain());
            provider.setBusinessHoursEnd(p.businessHoursEnd());
            provider.setBusinessHoursStart(p.businessHoursStart());
            provider.setName(p.name());
            provider.setPhone(p.phone());
            provider.setWebsite(p.website());
            provider.setWorkingDays(p.workingDays().stream().map((x) -> DayOfWeek.valueOf(x)).collect(Collectors.toSet()));

            return provider;
        });
    }

    @Test
    void deleteWhenResourceNotExists(){
        when(providerRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> {
            providerService.delete((500L));
        });
    }

    @Test
    void getPaginatedTest() {
        List<Provider> providers = new ArrayList<>();
        providers.add(TestObjectFactory.generateRandomisedProvider());
        providers.add(TestObjectFactory.generateRandomisedProvider());
        providers.add(TestObjectFactory.generateRandomisedProvider());
        providers.add(TestObjectFactory.generateRandomisedProvider());
        providers.add(TestObjectFactory.generateRandomisedProvider());

        when(providerRepository.findAll(isA(Pageable.class))).thenAnswer(i -> {
            Pageable pageable = (Pageable) i.getArguments()[0];
            return new PageImpl<Provider>(providers.subList(0, pageable.getPageSize()));
        });

        int pageableSize = 3;
        Pageable pageable = Pageable.ofSize(pageableSize);
        Page<ProviderDTOResponse> resultPage = providerService.getPaginated(pageable);
        List<ProviderDTOResponse> resultList = resultPage.stream().toList();

        assertThat(resultList.size()).isEqualTo(pageableSize);
        assertThat(resultPage).isNotEmpty();
    }

    @Test
    void getByIdTest(){
        Provider toBeRetrieved = new Provider(1L, "bname", "website.com", ".com", "12345123", mockStartTime, mockEndTime, testWorkingDays);
        ProviderDTOResponse providerDTOResponse = TestObjectFactory.generateProviderDtoResponse(toBeRetrieved);
        when(providerRepository.findById(anyLong())).thenReturn(Optional.of(toBeRetrieved));
        when(providerMapper.entityToDtoResponse(toBeRetrieved)).thenReturn(providerDTOResponse);
        ProviderDTOResponse returnedProvider = providerService.get(3000L);
        assertThat(returnedProvider.name()).isEqualTo(toBeRetrieved.getName());
    }
}