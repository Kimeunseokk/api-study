// 아이디 중복확인 버튼
const checkUsernameBtn = document.getElementById('check-username-btn');
const usernameInput = document.getElementById('username');
const usernameCheckResult = document.getElementById('username-check-result');

checkUsernameBtn.addEventListener('click', function () {
    const username = usernameInput.value.trim();

    if (!username) {
        alert('아이디를 먼저 입력해주세요.');
        return;
    }

    usernameCheckResult.textContent = '확인 중...';
    usernameCheckResult.className = '';

    fetch('/check-username?username=' + encodeURIComponent(username))
        .then(response => response.json())
        .then(exists => {
            if (exists) {
                usernameCheckResult.textContent = '이미 사용 중인 아이디입니다.';
                usernameCheckResult.className = 'taken';
            } else {
                usernameCheckResult.textContent = '사용 가능한 아이디입니다.';
                usernameCheckResult.className = 'available';
            }
        })
        .catch(error => {
            console.error('중복확인 요청 실패:', error);
            usernameCheckResult.textContent = '중복확인 중 오류가 발생했습니다.';
            usernameCheckResult.className = 'taken';
        });
});
