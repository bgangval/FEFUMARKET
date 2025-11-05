package com.example.fefumarket

object AdRepository {

    val ads: List<Ad> = listOf(
        Ad(
            title = "Ноутбук Lenovo",
            price = "₽45,000",
            dorm = "Корпус 8",
            seller = "Иван",
            description = "Новый, i7, 16GB RAM",
            imageResId = R.drawable.laptop_ic_test
        ),
        Ad(
            title = "Айфон 12",
            price = "₽70,000",
            dorm = "Корпус 5",
            seller = "Мария",
            description = "В отличном состоянии, 128GB",
            imageResId = R.drawable.iphone_ic_test
        ),
        Ad(
            title = "Кроссовки Nike",
            price = "₽9,000",
            dorm = "Корпус 3",
            seller = "Олег",
            description = "Размер 42, оригинал",
            imageResId = R.drawable.nike_ic_test
        ),
        Ad(
            title = "Монитор Samsung",
            price = "₽12,500",
            dorm = "Корпус 4",
            seller = "Анна",
            description = "27 дюймов, 4K",
            imageResId = R.drawable.monitor_ic_test
        ),
        Ad(
            title = "Наушники Sony",
            price = "₽5,000",
            dorm = "Корпус 2",
            seller = "Виктор",
            description = "Беспроводные, шумоподавление",
            imageResId = R.drawable.sony_ic_test
        ),
        Ad(
            title = "Велосипед Giant",
            price = "₽25,000",
            dorm = "Корпус 6",
            seller = "Петр",
            description = "Горный, 21 скорость",
            imageResId = R.drawable.bike_ic_test
        ),
        Ad(
            title = "Книга по Android",
            price = "₽1,200",
            dorm = "Корпус 1",
            seller = "Елена",
            description = "Kotlin для начинающих",
            imageResId = R.drawable.book_ic_test
        ),
        Ad(
            title = "Часы Casio",
            price = "₽3,500",
            dorm = "Корпус 7",
            seller = "Дмитрий",
            description = "Кварцевые, водонепроницаемые",
            imageResId = R.drawable.watch_ic_test
        )
    )

    fun findByTitle(title: String): Ad? {
        return ads.find { it.title.equals(title, ignoreCase = true) }
    }
}