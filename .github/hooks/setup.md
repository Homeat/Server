## 작동 원리

- prepare-commit-msg를 통해서 커밋 메시지를 미리 검사하는 것.
- 현재 브런치에 Jira 티켓넘버가 존재한다면, 커밋에 추가 시켜주는 것임.
- 커밋 내용에 이미 티켓넘버가 존재하면 추가하지 않음.

## 주의 사항

- 아래 설정 방법은 로컬(해당 레포 로컬 위치)에서만 적용되는 것.
- 따라서, 다른 레포에서는 작동되지 않음.
- 또한, 다른 개발 환경에서 새롭게 개발하게 되면, 해당 환경에 대한 재설정 필요.

## 설정법

1. source 폴더에서 .github/hooks 폴더 생성
2. .github/hooks 폴더에 prepare-commit-msg 파일 붙여넣기(생성)
3. `chmod +x .github/hooks/prepare-commit-msg`
4. `git config core.hooksPath .github/hooks`