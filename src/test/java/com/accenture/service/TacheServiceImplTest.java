package com.accenture.service;

import com.accenture.exception.TacheException;
import com.accenture.model.Priorite;
import com.accenture.repository.TacheDao;
import com.accenture.repository.entity.Tache;
import com.accenture.service.dto.TacheRequestDto;
import com.accenture.service.dto.TacheResponseDto;
import com.accenture.service.mapper.TacheMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class TacheServiceImplTest {

    @Mock
    TacheDao daoMock;

    @Mock
    TacheMapper mapperMock;

    @InjectMocks
    TacheServiceImpl service;


    @BeforeEach
    void init() {
        //      daoMock = Mockito.mock(TacheDao.class);
        //      mapperMock = Mockito.mock(TacheMapper.class);
        //      service = new TacheServiceImpl(daoMock, mapperMock);
    }

    @DisplayName("""
            Test de la méthode trouver(int id) qui doit renvoyer une exception 
            lorsque la tache n'existe pas en base
            """)
    @Test
    void testTrouverExistePas() {
        // simulation que la tache n'existe pas en base
        Mockito.when(daoMock.findById(50)).thenReturn(Optional.empty());

        // vérifier que la méthode trouver(50) renvoie bien une exception
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.trouver(50));
        assertEquals("id pas présent", ex.getMessage());
    }

    @DisplayName("""
            Test de la méthode trouver(int id) qui doit renvoyer un TacheResponseDto 
            lorsque la tache existe en base
            """)
    @Test
    void testTrouverExiste() {
        // simulation que la tache existe en base
        Tache t = creerTacheCadeaux();
        Optional<Tache> optTache = Optional.of(t);
        Mockito.when(daoMock.findById(1)).thenReturn(optTache);

        TacheResponseDto dto = creerTacheResponseDtoCadeaux();
        Mockito.when(mapperMock.toTacheResponseDto(t)).thenReturn(dto);

        assertSame(dto, service.trouver(1));

    }

    @DisplayName("""
            Test de la méthode trouverToutes qui doit renvoyer une liste de TacheResponseDto 
            correspondant aux taches existant en base
            """)
    @Test
    void testTrouverToutes() {
        Tache tacheCadeaux = creerTacheCadeaux();
        Tache tachePromenade = creerTachePromenade();

        List<Tache> taches = List.of(tacheCadeaux, tachePromenade);
        TacheResponseDto tacheResponseDtoCadeaux = creerTacheResponseDtoCadeaux();
        TacheResponseDto tacheResponseDtoPromenade = creerTacheResponseDtoPromenade();
        List<TacheResponseDto> dtos = List.of(tacheResponseDtoCadeaux, tacheResponseDtoPromenade);

        Mockito.when(daoMock.findAll()).thenReturn(taches);
        Mockito.when(mapperMock.toTacheResponseDto(tacheCadeaux)).thenReturn(tacheResponseDtoCadeaux);
        Mockito.when(mapperMock.toTacheResponseDto(tachePromenade)).thenReturn(tacheResponseDtoPromenade);

        assertEquals(dtos, service.trouverToutes());
    }



    @DisplayName(" Si ajouter(null) exception levée ")
    @Test
    void testAjouter(){
        assertThrows(TacheException.class, () -> service.ajouter(null));
    }


    @DisplayName(" Si ajouter(TacheRequestDto avec libelle null) exception levée ")
    @Test
    void testAjouterSansLibelle(){
        TacheRequestDto dto = new TacheRequestDto(null, LocalDate.now(), Priorite.BAS, true);
        assertThrows(TacheException.class, () -> service.ajouter(dto));
    }


    @DisplayName(" Si ajouter(TacheRequestDto avec libelle blank) exception levée ")
    @Test
    void testAjouterAvecLibelleBlank(){
        TacheRequestDto dto = new TacheRequestDto("  \t    \t   ", LocalDate.now(), Priorite.BAS, true);
        assertThrows(TacheException.class, () -> service.ajouter(dto));

        // "   ".trim() -> ""
        // "  \t ".trim() -> ""
        // "  \t a \t   ".trim() -> "a"
    }


    @DisplayName(" Si ajouter(TacheRequestDto avec dateLimite null) exception levée ")
    @Test
    void testAjouterSansDateLimite(){
        TacheRequestDto dto = new TacheRequestDto("La la la", null, Priorite.BAS, true);
        assertThrows(TacheException.class, () -> service.ajouter(dto));
    }

    @DisplayName(" Si ajouter(TacheRequestDto avec priorite null) exception levée ")
    @Test
    void testAjouterSansPriorite(){
        TacheRequestDto dto = new TacheRequestDto("La la la", LocalDate.now(), null, true);
        assertThrows(TacheException.class, () -> service.ajouter(dto));
    }

    @DisplayName(" Si ajouter(TacheRequestDto avec termine null) exception levée ")
    @Test
    void testAjouterSansTermine(){
        TacheRequestDto dto = new TacheRequestDto("La la la", LocalDate.now(), Priorite.BAS, null);
        assertThrows(TacheException.class, () -> service.ajouter(dto));
    }


    @DisplayName("""
            Si ajouter(TacheRequestDto OK) 
            alors save est appelé et
             TacheResponseDto renvoyé
            """)
    @Test
    void testAjouterOK(){
        TacheRequestDto requestDto = new TacheRequestDto("Balade dans le parc de Procé", LocalDate.of(2024,8, 12), Priorite.HAUT, true);
        Tache tacheAvantEnreg = creerTachePromenade();
        tacheAvantEnreg.setId(0);

        Tache tacheApresEnreg = creerTachePromenade();
        TacheResponseDto responseDto = creerTacheResponseDtoPromenade();

        Mockito.when(mapperMock.toTache(requestDto)).thenReturn(tacheAvantEnreg);
        Mockito.when(daoMock.save(tacheAvantEnreg)).thenReturn(tacheApresEnreg);
        Mockito.when(mapperMock.toTacheResponseDto(tacheApresEnreg)).thenReturn(responseDto);

        assertSame(responseDto, service.ajouter(requestDto));
        Mockito.verify(daoMock, Mockito.times(1)).save(tacheAvantEnreg);
    }


    private static Tache creerTacheCadeaux() {
        Tache t = new Tache();
        t.setId(1);
        t.setDateLimite(LocalDate.of(2024, 1, 24));
        t.setNiveau(Priorite.BAS);
        t.setLibelle("Acheter les cadeaux");
        t.setTermine(false);
        return t;
    }


    private static Tache creerTachePromenade() {
        Tache t = new Tache();
        t.setId(2);
        t.setDateLimite(LocalDate.of(2024, 8, 12));
        t.setNiveau(Priorite.HAUT);
        t.setLibelle("Balade dans le parc de Procé");
        t.setTermine(true);
        return t;
    }


    private static TacheResponseDto creerTacheResponseDtoCadeaux() {
        return new TacheResponseDto(
                1, "Acheter les cadeaux", LocalDate.of(2024, 12, 24), Priorite.BAS, false
        );
    }

    private static TacheResponseDto creerTacheResponseDtoPromenade() {
        return new TacheResponseDto(
                2, "Balade dans le parc de Procé", LocalDate.of(2024, 8, 13), Priorite.HAUT, true
        );
    }

}