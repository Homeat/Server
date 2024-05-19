package homeat.backend.domain.mypage.service;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.address.repository.AddressRepository;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import homeat.backend.domain.mypage.dto.MyPageRequest;
import homeat.backend.domain.mypage.dto.MyPageResponse;
import homeat.backend.domain.user.controller.MemberErrorStatus;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final AddressRepository addressRepository;
    private final FinanceDataRepository financeDataRepository;
    private final WeekRepository weekRepository;
    private final BCryptPasswordEncoder encoder;


    @Transactional
    public void insertInfo(Long memberId, MyPageRequest.postInfoDto request) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        Address selectedAddress = addressRepository.findById(request.getAdderessId())
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.ADDRESS_NOT_FOUND));

        selectedMember.updateNickname(request.getNickname());

        MemberInfo newMemberInfo = MyPageMapper.toMemberInfo(request, selectedMember, selectedAddress);
        memberInfoRepository.save(newMemberInfo);

        FinanceData newFinanceData = MyPageMapper.toFinanceData(selectedMember);
        financeDataRepository.save(newFinanceData);

        Week newWeek = MyPageMapper.toWeek(newFinanceData, request.getGoalPrice());
        weekRepository.save(newWeek);
    }

    public MyPageResponse.getInfoDto selectInfo(Long memberId) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));

        return MyPageResponse.getInfoDto.builder()
                .nickname(selectedMember.getNickname())
                .profileImgUrl(selectedMember.getProfileImgUrl())
                .build();
    }

    public MyPageResponse.getDetailInfoDto selectDetailInfo(Long memberId) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        MemberInfo selectedMemberInfo = memberInfoRepository.findMemberInfoByMember(selectedMember)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_INFO_NOT_FOUND));
        Address selectedAddress = selectedMemberInfo.getAddress();

        return MyPageResponse.getDetailInfoDto.builder()
                .email(selectedMember.getEmail())
                .nickname(selectedMember.getNickname())
                .profileImgUrl(selectedMember.getProfileImgUrl())

                .gender(selectedMemberInfo.getGender())
                .birth(selectedMemberInfo.getBirth())
                .income(selectedMemberInfo.getIncome())

                .address(AddressResponse.AddressDTO.builder()
                        .addressId(selectedAddress.getId())
                        .code(selectedAddress.getCode())
                        .fullNm(selectedAddress.getFullNm())
                        .emdNm(selectedAddress.getEmdNm())
                        .build())
                .build();
    }

    @Transactional
    public void updateInfo(Long memberId, MyPageRequest.patchInfoDto request) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        MemberInfo selectedMemberInfo = memberInfoRepository.findMemberInfoByMember(selectedMember)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_INFO_NOT_FOUND));

        if (request.getEmail() != null) selectedMember.updateEmail(request.getEmail());
        if (request.getNickname() != null) selectedMember.updateNickname(request.getNickname());
        if (request.getIncome() != null) selectedMemberInfo.updateIncome(request.getIncome());
        if (request.getAddressId() != null) {
            Address selectedAddress = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new GeneralException(MemberErrorStatus.ADDRESS_NOT_FOUND));
            selectedMemberInfo.updateAddress(selectedAddress);
        }
    }

    public void existNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname))
            throw new GeneralException(MemberErrorStatus.EXIST_NICKNAME);
    }

    @Transactional
    public void updatePassword(Long memberId, MyPageRequest.patchPasswordDto request) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));

        if (!encoder.matches(request.getOriginPassword(), selectedMember.getPassword()))
            throw new GeneralException(MemberErrorStatus.INVALID_PASSWORD);

        selectedMember.updatePassword(encoder.encode(request.getNewPassword()));
    }
}
