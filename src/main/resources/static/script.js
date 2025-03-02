document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('resultModal');
    const modalMessage = document.getElementById('modalMessage');
    const modalOkButton = document.getElementById('modalOkButton');
    const closeModal = document.querySelector('.close');

    function showModal(message, isError = false) {
        modalMessage.innerHTML = message;
        modalMessage.classList.toggle('error', isError);
        modal.style.display = 'block';
    }

    modalOkButton.addEventListener('click', function () {
        modal.style.display = 'none';
    });

    closeModal.addEventListener('click', function () {
        modal.style.display = 'none';
    });

    window.addEventListener('click', function (event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    function createCustomDropdown(dropdownId, endpoint, valueField, textField) {
        const dropdown = document.getElementById(dropdownId);
        if (!dropdown) return;

        dropdown.classList.add('hidden-select');

        const customDropdown = document.createElement('div');
        customDropdown.className = 'custom-dropdown';
        dropdown.parentNode.insertBefore(customDropdown, dropdown.nextSibling);

        const selectedOption = document.createElement('div');
        selectedOption.className = 'selected-option';
        selectedOption.textContent = 'Выберите...';
        customDropdown.appendChild(selectedOption);

        const optionsContainer = document.createElement('div');
        optionsContainer.className = 'options-container';
        customDropdown.appendChild(optionsContainer);

        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = dropdown.name;
        hiddenInput.id = dropdown.id;
        hiddenInput.required = dropdown.required; // Добавляем required для валидации
        dropdown.parentNode.insertBefore(hiddenInput, dropdown.nextSibling);

        dropdown.remove();

        selectedOption.addEventListener('click', function () {
            if (optionsContainer.style.display === 'block') {
                optionsContainer.style.display = 'none';
            } else {
                fetch(endpoint)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Ошибка при загрузке данных');
                        }
                        return response.json();
                    })
                    .then(data => {
                        optionsContainer.innerHTML = '';

                        data.forEach(item => {
                            const option = document.createElement('div');
                            option.className = 'option';
                            option.textContent = item[textField];
                            option.dataset.value = item[valueField];
                            option.addEventListener('click', function () {
                                selectedOption.textContent = item[textField];
                                hiddenInput.value = item[valueField];
                                optionsContainer.style.display = 'none';
                            });
                            optionsContainer.appendChild(option);
                        });

                        optionsContainer.style.display = 'block';
                    })
                    .catch(error => {
                        console.error('Ошибка при загрузке данных:', error);
                        showModal(`Ошибка: ${error.message}`, true);
                    });
            }
        });

        document.addEventListener('click', function (event) {
            if (!customDropdown.contains(event.target)) {
                optionsContainer.style.display = 'none';
            }
        });
    }

    createCustomDropdown('filmGenre', '/genres', 'genreId', 'name');
    createCustomDropdown('seatHall', '/halls', 'hallId', 'alternativeName');
    createCustomDropdown('sessionHall', '/halls', 'hallId', 'alternativeName');
    createCustomDropdown('sessionFilm', '/films', 'filmId', 'name');
    createCustomDropdown('seatType', '/seat-types', 'seatTypeId', 'name');
    createCustomDropdown('bookingCustomer', '/customers', 'customerId', 'fullName');

    function handleFormSubmit(formId, endpoint, successMessage) {
        const form = document.getElementById(formId);
        if (!form) return;

        form.addEventListener('submit', function (event) {
            event.preventDefault();

            // Проверка выбранного значения в dropdown-ах
            const dropdowns = form.querySelectorAll('input[type="hidden"][required]');
            let isValid = true;

            dropdowns.forEach(dropdown => {
                if (!dropdown.value) {
                    isValid = false;
                    showModal(`Ошибка: Поле "${dropdown.name}" обязательно для заполнения`, true);
                }
            });

            if (!isValid) return;

            const formData = new FormData(form);
            const data = {};
            formData.forEach((value, key) => {
                if (key === 'bookingTime' || key === 'startTime') {
                    data[key] = new Date(value).toISOString(); // Конвертация для bookingTime и startTime
                } else {
                    data[key] = value;
                }
            });

            fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => {
                            throw new Error(err.details || 'Произошла ошибка на сервере');
                        });
                    }
                    return response.json();
                })
                .then(result => {
                    showModal(successMessage(result));
                    form.reset();
                })
                .catch(error => {
                    showModal(`Ошибка: ${error.message}`, true);
                });
        });
    }

    const successMessages = {
        customerForm: (result) => `
            Клиент успешно создан!<br>
            ID: ${result.customerId}<br>
            Полное имя: ${result.fullName}<br>
            Баланс: ${result.balance}
        `,
        bookingForm: (result) => `
            Бронирование успешно создано!<br>
            ID: ${result.bookingId}<br>
            Время бронирования: ${result.bookingTime}<br>
            Место в сеансе: ${result.sessionSeatId}<br>
            Клиент: ${result.customerId}
        `,
        filmForm: (result) => `
            Фильм успешно создан!<br>
            ID: ${result.filmId}<br>
            Название: ${result.name}<br>
            Жанр: ${result.genreId}<br>
            Длительность: ${result.duration}
        `,
        genreForm: (result) => `
            Жанр успешно создан!<br>
            ID: ${result.genreId}<br>
            Название: ${result.name}
        `,
        hallForm: (result) => `
            Зал успешно создан!<br>
            ID: ${result.hallId}<br>
            Название: ${result.alternativeName}
        `,
        seatForm: (result) => `
            Место успешно создано!<br>
            ID: ${result.seatId}<br>
            Зал: ${result.hallId}<br>
            Ряд: ${result.rowNumber}<br>
            Место: ${result.seatNumber}<br>
            Тип места: ${result.seatTypeId}
        `,
        seatTypeForm: (result) => `
            Тип места успешно создан!<br>
            ID: ${result.seatTypeId}<br>
            Название: ${result.name}
        `,
        sessionForm: (result) => `
            Сеанс успешно создан!<br>
            ID: ${result.sessionId}<br>
            Зал: ${result.hallId}<br>
            Фильм: ${result.filmId}<br>
            Время начала: ${result.startTime}
        `,
        sessionSeatForm: (result) => `
            Связка "Сеанс-Место" успешно создана!<br>
            ID: ${result.sessionSeatId}<br>
            Сеанс: ${result.sessionId}<br>
            Место: ${result.seatId}<br>
            Цена: ${result.price}
        `,
    };

    if (document.getElementById('customerForm')) {
        handleFormSubmit('customerForm', '/customers', successMessages.customerForm);
    }
    if (document.getElementById('bookingForm')) {
        handleFormSubmit('bookingForm', '/bookings', successMessages.bookingForm);
    }
    if (document.getElementById('filmForm')) {
        handleFormSubmit('filmForm', '/films', successMessages.filmForm);
    }
    if (document.getElementById('genreForm')) {
        handleFormSubmit('genreForm', '/genres', successMessages.genreForm);
    }
    if (document.getElementById('hallForm')) {
        handleFormSubmit('hallForm', '/halls', successMessages.hallForm);
    }
    if (document.getElementById('seatForm')) {
        handleFormSubmit('seatForm', '/seats', successMessages.seatForm);
    }
    if (document.getElementById('seatTypeForm')) {
        handleFormSubmit('seatTypeForm', '/seat-types', successMessages.seatTypeForm);
    }
    if (document.getElementById('sessionForm')) {
        handleFormSubmit('sessionForm', '/sessions', successMessages.sessionForm);
    }
    if (document.getElementById('sessionSeatForm')) {
        handleFormSubmit('sessionSeatForm', '/session-seats', successMessages.sessionSeatForm);
    }
});