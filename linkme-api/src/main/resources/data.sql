IF NOT EXISTS(SELECT email FROM users WHERE email = 'hr@company.com')
BEGIN
INSERT INTO users (email, password, name, recruiter) VALUES
  ('hr@company.com', 'test1', 'Company HR', 1),
  ('nadeemiimt@gmail.com', 'test2', 'Mohd Nadeem', 0);
END