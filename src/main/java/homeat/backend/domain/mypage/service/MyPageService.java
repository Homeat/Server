package homeat.backend.domain.mypage.service;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.address.repository.AddressRepository;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.WeekAnalyze;
import homeat.backend.domain.homeatreport.entity.WeekCheck;
import homeat.backend.domain.homeatreport.repository.WeekAnalyzeRepository;
import homeat.backend.domain.homeatreport.repository.WeekCheckRepository;
import homeat.backend.domain.homeatreport.service.HomeatReportAnalyzeService;
import homeat.backend.domain.mypage.dto.MyPageRequest;
import homeat.backend.domain.mypage.dto.MyPageResponse;
import homeat.backend.domain.user.controller.MemberErrorStatus;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final AddressRepository addressRepository;
    private final FinanceDataRepository financeDataRepository;
    private final WeekCheckRepository weekCheckRepository;
    private final WeekAnalyzeRepository weekAnalyzeRepository;
    private final S3Service s3Service;
    private final BCryptPasswordEncoder encoder;
    private final HomeatReportAnalyzeService homeatReportAnalyzeService;

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
        WeekCheck newWeekCheck = MyPageMapper.toWeekCheck(newFinanceData, request.getGoalPrice());

        Integer currentWeekIdx = homeatReportAnalyzeService.findWeekIdx(LocalDate.now());
        WeekAnalyze newWeekAnalyze = MyPageMapper.toWeekAnalyze(newFinanceData, currentWeekIdx);

        financeDataRepository.save(newFinanceData);
        weekCheckRepository.save(newWeekCheck);
        weekAnalyzeRepository.save(newWeekAnalyze);
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

    @Transactional
    public void updateProfileImg(Long memberId, MultipartFile profileImg) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        String newProfileImgUrl = s3Service.uploadProfileImg(profileImg);

        if (!selectedMember.getProfileImgUrl().equals("https://homeat-dev-s3.s3.ap-northeast-2.amazonaws.com/homeat/default/default_icon.png"))
            s3Service.fileDelete(selectedMember.getProfileImgUrl());
        selectedMember.updateProfileImgUrl(newProfileImgUrl);
    }

    @Transactional
    public void deleteProfileImg(Long memberId) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));

        if (!selectedMember.getProfileImgUrl().equals("https://homeat-dev-s3.s3.ap-northeast-2.amazonaws.com/homeat/default/default_icon.png"))
            s3Service.fileDelete(selectedMember.getProfileImgUrl());
        selectedMember.updateProfileImgUrl("https://homeat-dev-s3.s3.ap-northeast-2.amazonaws.com/homeat/default/default_icon.png");
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        selectedMember.withdraw();
    }

    @Transactional
    public void reactivate(Long memberId) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        selectedMember.reactivate();
    }
}