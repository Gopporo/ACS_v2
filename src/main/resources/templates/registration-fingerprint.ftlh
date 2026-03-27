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

    <div class="alert alert-info" role="alert" id="statusBox">
        Ожидаем сканирование отпечатка...
    </div>

    <p class="mb-3">
        Просканируйте палец на считывателе. После успешного сохранения отпечатка страница автоматически
        перейдет на вход в систему.
    </p>

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
    let done = false;

    async function pollStatus() {
        if (done) return;
        try {
            const res = await fetch('/registration/fingerprint/status?userId=' + encodeURIComponent(userId), {
                headers: { 'Accept': 'application/json' }
            });
            const data = await res.json();
            if (data.fingerprintSet) {
                done = true;
                statusBox.textContent = 'Отпечаток сохранен.';
                afterDoneBlock.style.display = 'block';
            } else {
                statusBox.textContent = 'Ожидаем сканирование отпечатка...';
            }
        } catch (e) {
            statusBox.textContent = 'Не удалось проверить статус. Попробуйте позже.';
        }
    }

    pollStatus();
    setInterval(pollStatus, 2000);
</script>
</body>
</html>

