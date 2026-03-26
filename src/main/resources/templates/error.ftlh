<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container py-5">
    <h1 class="mb-3">Произошла ошибка</h1>
    <#if errorMessage??>
        <div class="alert alert-danger" role="alert">${errorMessage}</div>
    <#else>
        <div class="alert alert-danger" role="alert">Попробуйте позже.</div>
    </#if>
    <a href="/index" class="btn btn-primary mt-2">На главную</a>
</div>
</body>
</html>

