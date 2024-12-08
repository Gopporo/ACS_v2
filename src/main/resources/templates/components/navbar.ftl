<navbar>
    <div class="navbar navbar-dark bg-dark shadow-sm">
        <div class="container d-flex align-items-center justify-content-between">
            <a href="/" class="navbar-brand d-flex align-items-center">
                <!-- Иконка замка -->
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" aria-hidden="true" class="me-2" viewBox="0 0 24 24">
                    <rect x="5" y="10" width="14" height="10" rx="2" ry="2"/> <!-- Корпус замка -->
                    <path d="M8 10V6a4 4 0 1 1 8 0v4"/> <!-- Дуга замка -->
                </svg>
                <strong>Access Control System</strong>
            </a>

            <div class="d-flex align-items-center ms-auto">
                <#if role??>
                    <#switch role>
                        <#case "ROLE_USER">
                            <a href="/applications" class="btn btn-outline-light me-2">Заявки</a>
                            <a href="/applications/my" class="btn btn-outline-light me-2">Мои заявки</a>
                            <a href="/reports/my" class="btn btn-outline-light me-2">Мои отчеты</a>
                            <#break>
                        <#case "ROLE_DIRECTOR">
                            <a href="/users" class="btn btn-outline-light me-2">Сотрудники</a>
                            <a href="/applications" class="btn btn-outline-light me-2">Заявки</a>
                            <a href="/reports" class="btn btn-outline-light me-2">Отчеты</a>
                            <#break>
                        <#case "ROLE_ADMIN">
                            <a href="/admin/users" class="btn btn-outline-light me-2">Пользователи</a>
                            <a href="/admin/zones" class="btn btn-outline-light me-2">Зоны</a>
                            <a href="/admin/departments" class="btn btn-outline-light me-2">Отделы</a>
                            <#break>
                    </#switch>
                    <a href="/user/${userId}" class="btn btn-outline-light me-2 d-flex align-items-center profile-btn">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" aria-hidden="true" class="me-1" viewBox="0 0 24 24">
                            <circle cx="12" cy="8" r="4"/> <!-- Голова -->
                            <path d="M6 18c0-3.33 5-5 6-5s6 1.67 6 5"/> <!-- Тело -->
                        </svg>
                        <span>Профиль</span>
                    </a>
                <#else>
                    <a class="btn btn-outline-light me-2" href="/login">Login</a>
                </#if>
            </div>
        </div>
    </div>
</navbar>