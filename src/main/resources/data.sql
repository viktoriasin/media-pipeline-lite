INSERT INTO content (id, title, duration_seconds, content_path) VALUES
(1, 'Big Buck Bunny', 120, '/content/demo');

INSERT INTO timeline_event (content_id, type, start_time_seconds, end_time_seconds, label, skippable) VALUES
(1, 'INTRO', 0, 10, 'Opening', true),
(1, 'CHAPTER', 10, 40, 'Scene 1', false),
(1, 'CHAPTER', 40, 70, 'Scene 2', false),
(1, 'CHAPTER', 70, 100, 'Scene 3', false),
(1, 'CHAPTER', 100, 120, 'Scene 4', false);
