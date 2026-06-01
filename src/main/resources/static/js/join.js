// 1. 가입 버튼(또는 form) 요소를 가져옵니다.
const registerButton = document.getElementById('register-btn');

// 2. 버튼에 클릭 이벤트를 달아줍니다.
registerButton.addEventListener('click', function(e) {
    // 💡 중요! 폼 제출 시 브라우저가 제멋대로 새로고침하는 걸 막아줍니다.
    e.preventDefault();

    // (여기에 사용자가 입력한 아이디, 비밀번호 등을 가져오는 코드 작성)
    const userData = {
        /* 예: username: document.getElementById('id').value ... */
    };

    // 3. 서버(스프링 부트)로 데이터를 보냅니다.
    fetch('/register', { // 스프링 부트 컨트롤러의 주소
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
    })
    .then(response => {
        // 4. 서버에서 저장 성공 신호(예: 200 OK)가 왔다면?
        if (response.ok) {
            alert('회원가입이 완료되었습니다!');       // 알림창 띄우기
            window.location.href = '/login';       // 로그인 페이지로 이동하기! (경로는 프로젝트에 맞게 수정)
        } else {
            alert('회원가입에 실패했습니다. 다시 시도해 주세요.');
        }
    })
    .catch(error => {
        console.error('에러 발생:', error);
    });
});