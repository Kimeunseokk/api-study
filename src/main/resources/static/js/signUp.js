// 회원가입 폼 제출 전 간단한 검증 예시
function validateSignup() {
    const password = document.getElementById('password').value;
    const passwordCheck = document.getElementById('passwordCheck').value;

    if (password !== passwordCheck) {
        alert('비밀번호가 일치하지 않습니다.');
        return false; // 폼 전송 중단
    }
    return true; // 폼 전송 진행
}