class Schedule:
    id = ""
    mac = ""
    week_day = 0
    hour = 0
    date_create = ""
    date_update = ""
    deleted = 0

    def __init__(self, id, mac, week_day, hour, date_create, date_update, deleted):
        self.id = id
        self.mac = mac
        self.week_day = week_day
        self.hour = hour
        self.date_create = date_create
        self.date_update = date_update
        self.deleted = deleted

    def __eq__(self, other):
        return self.id == other.get_id()

    def get_id(self):
        return self.id

    def get_mac(self):
        return self.mac

    def get_week_day(self) -> int:
        return int(self.week_day)

    def get_hour(self) -> int:
        return int(self.hour)

    def get_date_create(self):
        return self.date_create

    def get_date_update(self):
        return self.date_update

    def get_deleted(self):
        return self.deleted
