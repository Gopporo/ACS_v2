<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Регистрация отпечатка</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4" style="max-width: 750px;">
    <h1 class="h3 mb-3">Регистрация отпечатка пальца</h1>

    <div class="alert alert-info" role="alert" id="statusBox">Ожидаем сканирование отпечатка...</div>

    <p class="mb-3">
        Просканируйте палец на считывателе. После успешного сохранения отпечатка страница автоматически
        перейдет на вход в систему.
    </p>

    <div class="d-flex gap-2 mb-3">
        <button class="btn btn-dark" id="retryBtn" type="button">Повторить сканирование</button>
        <form action="/registration/fingerprint/cancel" method="post" class="m-0">
            <input type="hidden" name="userId" value="${userId}">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button class="btn btn-outline-secondary" type="submit">Отменить</button>
        </form>
    </div>

    <noscript>
        Пожалуйста, включите JavaScript, чтобы система могла проверить статус регистрации отпечатка.
    </noscript>

    <input type="hidden" id="userId" value="${userId}">

    <div class="mt-3" id="afterDoneBlock" style="display: none;">
        <div class="alert alert-success mb-0">
            Отпечаток сохранен. Дальше дождитесь одобрения администратора.
        </div>
        <div class="mt-3">
            <a class="btn btn-dark" href="/login">Перейти на страницу входа</a>
        </div>
    </div>
</main>

<script>
    const userId = document.getElementById('userId').value;
    const statusBox = document.getElementById('statusBox');
    const afterDoneBlock = document.getElementById('afterDoneBlock');
    const retryBtn = document.getElementById('retryBtn');
    let done = false;

    function setStatusInfo(text) {
        statusBox.className = 'alert alert-info';
        statusBox.textContent = text;
    }

    function setStatusError(text) {
        statusBox.className = 'alert alert-danger';
        statusBox.textContent = text;
    }

    async function pollStatus() {
        if (done) return;
        try {
            const res = await fetch('/registration/fingerprint/status?userId=' + encodeURIComponent(userId), {
                headers: { 'Accept': 'application/json' }
            });
            const data = await res.json();
            if (data.state === 'IN_PROGRESS' || !data.state) {
                setStatusInfo('Ожидаем сканирование отпечатка...');
                return;
            }

            if (data.state === 'SUCCESS') {
                done = true;
                statusBox.style.display = 'none';
                afterDoneBlock.style.display = 'block';
                return;
            }

            if (data.state === 'ERROR') {
                setStatusError('Сканирование не удалось. Нажмите «Повторить сканирование» и попробуйте ещё раз.');
                return;
            }

            if (data.state === 'CANCELLED') {
                setStatusInfo('Процесс отменён.');
                return;
            }

            setStatusInfo('Ожидаем сканирование отпечатка...');
        } catch (e) {
            setStatusError('Не удалось проверить статус. Попробуйте позже.');
        }
    }

    retryBtn.addEventListener('click', async function () {
        if (done) {
            return;
        }
        retryBtn.disabled = true;
        try {
            await fetch('/registration/fingerprint/retry?userId=' + encodeURIComponent(userId), {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'X-CSRF-TOKEN': '${_csrf.token}'
                }
            });
            setStatusInfo('Ожидаем сканирование отпечатка...');
        } catch (e) {
            setStatusError('Не удалось перезапустить сканирование.');
        } finally {
            retryBtn.disabled = false;
        }
    });

    pollStatus();
    setInterval(pollStatus, 2000);
</script>
</body>
</html>

