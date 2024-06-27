UPDATE project
SET status = 'To Start'
WHERE status <> 'In Progress';

UPDATE project
SET status = 'In Progress'
WHERE status <> 'in progress';


