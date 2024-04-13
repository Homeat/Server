1. source 폴더에서 .github/hooks 폴더 생성
2. .github/hooks 폴더에 prepare-commit-msg 파일 붙여넣기(생성)
3. `chmod +x .github/hooks/prepare-commit-msg`
4. `git config core.hooksPath .github/hooks`