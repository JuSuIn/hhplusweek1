package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    //1.유저의 저장소.
    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;
    @InjectMocks
    private PointService pointService;


    //1.유저 포인트 조회
    //1) 특정 유저의 포인트가 잘조회되는지 테스트를 해본다.
    //2) 존재하지 않는 유저 조회할떄 (404반환)
    //3) 특정 유저의 아이디명을 입력을 안했을 때
    @Test
    public void test_Get_userPoint_By_Userid() throws Exception {
        long user_id= 1L; long user_point=10000L; long updatemillis=System.currentTimeMillis();
        UserPoint mkUserPoint= new UserPoint(user_id,user_point,updatemillis);

         //특정값 반환
        when(userPointTable.selectById(user_id)).thenReturn(mkUserPoint);
        UserPoint result = userPointTable.selectById(user_id);

        //1.조회확인
        assertEquals(user_point,result.point());
        // 2. 존재하지 않는 유저 예외 발생 확인
        long invalidUserId=121L;
        when(userPointTable.selectById(invalidUserId)).thenReturn(null);
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> pointService.getPointNotfoundUserId(invalidUserId));

        // 예외 메시지 확인 (옵션)
        assertEquals("User not found for id: " + invalidUserId, thrown.getMessage());

        // 3.잘못된 데이터 (예: userId가 음수)
        long negativeUserId = -1L;
        when(userPointTable.selectById(negativeUserId)).thenReturn(mkUserPoint);
        assertThrows(IllegalArgumentException.class, () -> pointService.getIllegalArgErrorUserId(negativeUserId));
    }

    //2. 특정 유저의 포인트 충전/이용 내역을 조회 테스트
    @Test
    public void test_Get_user_Point_ChangeHis(){
        long user_id= 1L; long user_point=5000L; long updatemillis=System.currentTimeMillis();
        //충전 및 이용내역 조회
        PointHistory pointChangeHis= new PointHistory(1,user_id,user_point,TransactionType.CHARGE,updatemillis);
        when(pointHistoryTable.selectAllByUserId(user_id))
                .thenReturn(List.of(pointChangeHis));

        List<PointHistory> result= pointHistoryTable.selectAllByUserId(user_id);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5000L,result.get(0).amount());
        assertEquals(TransactionType.CHARGE,result.get(0).type());

        //test_Get_user_Point_Change(result);
    }

    //3. 특정유저의 포인트를 충전
    @Test
    void test_Get_user_Point_Change(){
        long user_id= 1L;
        long user_point=10000L; long updatemillis=System.currentTimeMillis();
        long chargeAmount = 500L;
        UserPoint mkUserPoint= new UserPoint(user_id,user_point,updatemillis);

        //특정값 반환
        when(userPointTable.selectById(user_id)).thenReturn(mkUserPoint);
        UserPoint result = userPointTable.selectById(user_id);

        doAnswer(invocation -> {
            long id = invocation.getArgument(0);
            long amount = invocation.getArgument(1);
            long newBalance = result.point()+chargeAmount;
            System.out.println("포인트 충전 완료! 새로운 잔액: " + newBalance);
            return null; // void 메서드이므로 return 없음
        }).when(userPointTable).insertOrUpdate(anyLong(),anyLong());

        userPointTable.insertOrUpdate(user_id,chargeAmount);

        verify(userPointTable,times(1)).insertOrUpdate(user_id,chargeAmount);
    }

//    //4. 특정유저의 포인트를 사용
//    @Test
//    public void test_Get_user_Point_Useing(){
//
//    }

}